def call() {

    switch(env.LANG) {

        case "python":

            // 🔍 REAL AUTO DETECTION (no hardcoding)
            def reqFile = sh(
                script: "find . -type f -name requirements.txt | head -n 1",
                returnStdout: true
            ).trim()

            if (reqFile == "") {
                error "requirements.txt not found anywhere in workspace"
            }

            // extract folder path
            def pythonRoot = reqFile.replace("/requirements.txt", "")
            if (pythonRoot == "requirements.txt") {
                pythonRoot = "."
            }

            echo "Detected Python project root: ${pythonRoot}"

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
