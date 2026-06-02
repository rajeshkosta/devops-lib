def call() {

    switch(env.LANG) {

        case "java":
            sh 'mvn test'
            break

        case "node":
            sh 'npm test || true'
            break

        case "python":
            sh '''
            . venv/bin/activate
            pytest
            '''
            break

        case "go":
            sh 'go test ./...'
            break

        default:
            echo "No Tests"
    }
}
