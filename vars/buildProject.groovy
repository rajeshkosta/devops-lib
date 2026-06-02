def call() {

    switch(env.LANG) {

        case "java":
            sh 'mvn clean package -DskipTests'
            break

        case "node":
            sh '''
            npm install
            npm run build || true
            '''
            break

        case "python":
            sh '''
            docker run --rm \
                -v ${WORKSPACE}:/app \
                -w /app \
                python:3.12 \
                sh -c "pip install -r requirements.txt && pytest"
            '''
            break

        case "go":
            sh '''
            go mod download
            go build -o app
            '''
            break

        default:
            error("Unsupported language")
    }
}
