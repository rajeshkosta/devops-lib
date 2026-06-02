def call() {

    switch(env.LANG) {

        case "python":

            def pythonRoot = null

            // check root first
            if (fileExists("requirements.txt")) {
                pythonRoot = "."
            }
            // check Application-Code
            else if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            }

            if (pythonRoot == null) {
                error "requirements.txt not found in root OR Application-Code"
            }

            echo "Detected Python project location: ${pythonRoot}"

            sh """
            docker run --rm \
            -v \$PWD:/app \
            -w /app/${pythonRoot} \
            python:3.12 \
            sh -c "
                ls -la &&
                pip install -r requirements.txt &&
                pytest
            "
            """

            break


        case "java":
            sh 'mvn clean package -DskipTests'
            break


        case "node":
            sh '''
            npm install
            npm run build || true
            '''
            break


        case "go":
            sh '''
            go mod download
            go build -o app
            '''
            break


        default:
            error "Unsupported language: ${env.LANG}"
    }
}
