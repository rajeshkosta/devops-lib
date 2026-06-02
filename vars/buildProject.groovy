def call() {

    switch(env.LANG) {

        case "python":

            def pythonRoot = ""

            // requirements.txt in repo root
            if (fileExists("requirements.txt")) {
                pythonRoot = "."
            }
            // requirements.txt in Application-Code
            else if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            }
            else {
                error """
requirements.txt not found.

Expected one of:
- ./requirements.txt
- ./Application-Code/requirements.txt
"""
            }

            echo "Detected Python project root: ${pythonRoot}"

            // Debug output
            sh """
                echo "Current workspace:"
                pwd

                echo "Root contents:"
                ls -la

                echo "Project contents:"
                ls -la ${pythonRoot}
            """

            sh """
                docker run --rm \
                -v \$PWD:/app \
                -w /app/${pythonRoot} \
                python:3.12 \
                sh -c 'pip install -r requirements.txt && pytest'
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
