def call() {
    switch(env.LANG) {
        case "python":
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
            echo "Detected Python project root: ${pythonRoot}"

            // Resolve the real host-side workspace path (fixes DinD volume mount issue)
            def hostWorkspace = sh(
                script: """
                    # Try to get host path from container inspect (works when Jenkins runs in Docker)
                    CONTAINER_ID=\$(cat /etc/hostname)
                    HOST_PATH=\$(docker inspect \$CONTAINER_ID \
                        --format '{{ range .Mounts }}{{ if eq .Destination "/var/jenkins_home" }}{{ .Source }}{{ end }}{{ end }}' 2>/dev/null || true)
                    
                    if [ -n "\$HOST_PATH" ]; then
                        # Replace /var/jenkins_home prefix with host path
                        echo "${env.WORKSPACE}" | sed "s|/var/jenkins_home|\$HOST_PATH|"
                    else
                        # Fallback: Jenkins is not in Docker, use WORKSPACE directly
                        echo "${env.WORKSPACE}"
                    fi
                """,
                returnStdout: true
            ).trim()

            echo "Resolved host workspace: ${hostWorkspace}"

            // Docker debug
            sh """
                docker run --rm \\
                  -v ${hostWorkspace}:/app \\
                  -w /app/${pythonRoot} \\
                  python:3.12 \\
                  sh -c '
                    echo "===== INSIDE CONTAINER ====="
                    pwd
                    ls -la
                    echo "===== REQUIREMENTS ====="
                    cat requirements.txt
                  '
            """

            // Build & Test
            sh """
                docker run --rm \\
                  -v ${hostWorkspace}:/app \\
                  -w /app/${pythonRoot} \\
                  python:3.12 \\
                  sh -c '
                    pip install -r requirements.txt &&
                    python -m pytest --tb=short || exit 1
                  '
            """
            break

        case "java":
            echo "Building Java project..."
            sh 'mvn clean package -DskipTests'
            break

        case "node":
            echo "Building Node project..."
            sh '''
                npm install
                npm run build || true
            '''
            break

        case "go":
            echo "Building Go project..."
            sh '''
                go mod download
                go build -o app
            '''
            break

        default:
            error "Unsupported language: ${env.LANG}"
    }
}
