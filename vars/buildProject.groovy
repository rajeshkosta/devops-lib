def call() {

    switch(env.LANG) {

        case "python":

            def pythonRoot = ""

            // check root first
            if (fileExists("requirements.txt")) {
                pythonRoot = "."
            }
            // check Application-Code folder
            else if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            }
            else {
                error "requirements.txt not found in root or Application-Code"
            }

            echo "Detected Python project location: ${pythonRoot}"

            sh """
            docker run --rm \
            -v \$PWD:/app \
            -w /app/${pythonRoot} \
            python:3.12 \
            sh -c "
                pip install -r requirements.txt && pytest
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
