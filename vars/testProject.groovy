def call() {

    switch(env.LANG) {

        case "java":
            sh 'mvn test'
            break

        case "node":
            sh 'npm test'
            break

        case "python":
            sh '''
            docker run --rm \
                -v ${WORKSPACE}:/app \
                -w /app \
                python:3.12 \
                bash -c "pip install -r requirements.txt && pytest"
            '''
            break

        case "go":
            sh 'go test ./...'
            break

        default:
            echo "No Tests"
    }
}
