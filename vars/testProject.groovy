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

            sh """
            docker run --rm \
              -v ${env.WORKSPACE}:/app \
              -w /app/${pythonRoot} \
              python:3.12 \
              sh -c '
                pip install -r requirements.txt
              '
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
