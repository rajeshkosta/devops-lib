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

            def hostWorkspace = sh(
                script: """
                    CONTAINER_ID=\$(cat /etc/hostname)

                    HOST_PATH=\$(docker inspect \$CONTAINER_ID \
                        --format '{{ range .Mounts }}{{ if eq .Destination "/var/jenkins_home" }}{{ .Source }}{{ end }}{{ end }}' \
                        2>/dev/null || true)

                    if [ -n "\$HOST_PATH" ]; then
                        echo "${env.WORKSPACE}" | sed "s|/var/jenkins_home|\$HOST_PATH|"
                    else
                        echo "${env.WORKSPACE}"
                    fi
                """,
                returnStdout: true
            ).trim()

            echo "Resolved host workspace: ${hostWorkspace}"

            sh """
                docker run --rm \
                  -v ${hostWorkspace}:/app \
                  -w /app/${pythonRoot} \
                  python:3.12 \
                  sh -c '
                    python -m pip install --upgrade pip
                    pip install -r requirements.txt
                  '
            """
            break


        case "java":

            echo "Building Java project..."

            sh '''
                mvn clean package -DskipTests
            '''
            break


        case "node":

            echo "Building Node.js project..."

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
