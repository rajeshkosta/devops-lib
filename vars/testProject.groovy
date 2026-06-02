def call() {
    switch(env.LANG) {
        case "python":
            // Resolve host-side workspace path (fixes DinD volume mount issue)
            def hostWorkspace = sh(
                script: """
                    CONTAINER_ID=\$(cat /etc/hostname)
                    HOST_PATH=\$(docker inspect \$CONTAINER_ID \
                        --format '{{ range .Mounts }}{{ if eq .Destination "/var/jenkins_home" }}{{ .Source }}{{ end }}{{ end }}' 2>/dev/null || true)

                    if [ -n "\$HOST_PATH" ]; then
                        echo "${env.WORKSPACE}" | sed "s|/var/jenkins_home|\$HOST_PATH|"
                    else
                        echo "${env.WORKSPACE}"
                    fi
                """,
                returnStdout: true
            ).trim()

            // Detect project root
            def pythonRoot = "."
            if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            } else if (!fileExists("requirements.txt")) {
                error """
requirements.txt not found.
Expected one of:
- ./requirements.txt
- ./Application-Code/requirements.txt
"""
            }

            // Detect dev requirements file
            def reqFile = fileExists("${pythonRoot}/requirements-dev.txt")
                ? "requirements-dev.txt"
                : "requirements.txt"

            echo "Python root: ${pythonRoot} | Requirements: ${reqFile}"

            sh """
                docker run --rm \\
                  -v ${hostWorkspace}:/app \\
                  -w /app/${pythonRoot} \\
                  python:3.12 \\
                  sh -c '
                    pip install --quiet --no-cache-dir --root-user-action=ignore -r ${reqFile} &&
                    pip install --quiet --no-cache-dir --root-user-action=ignore pytest &&
                    python -m pytest --tb=short || exit 1
                  '
            """
            break

        case "java":
            echo "Running Java tests..."
            sh 'mvn test'
            break

        case "node":
            echo "Running Node tests..."
            sh 'npm test'
            break

        case "go":
            echo "Running Go tests..."
            sh 'go test ./...'
            break

        default:
            echo "No tests configured for language: ${env.LANG}"
    }
}
