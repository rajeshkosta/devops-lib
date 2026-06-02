def call() {

    switch(env.LANG) {

        case "python":

            def pythonRoot = "."

            // Check if Python project is inside Application-Code
            if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            }
            // Otherwise verify requirements.txt exists in root
            else if (!fileExists("requirements.txt")) {
                error """
requirements.txt not found.

Expected one of:
- ./requirements.txt
- ./Application-Code/requirements.txt
"""
            }

            echo "Detected Python project root: ${pythonRoot}"

            // Jenkins workspace debug
            sh """
                echo "===== WORKSPACE ====="
                pwd

                echo "===== ROOT CONTENTS ====="
                ls -la

                echo "===== PROJECT CONTENTS ====="
                ls -la ${pythonRoot}
            """

            // Docker debug
            sh """
            docker run --rm \
              -v \$PWD:/app \
              -w /app/${pythonRoot} \
              python:3.12 \
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
            docker run --rm \
              -v \$PWD:/app \
              -w /app/${pythonRoot} \
              python:3.12 \
              sh -c '
                pip install -r requirements.txt &&
                pytest
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
