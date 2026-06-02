def call() {

    switch(env.LANG) {

        case "java":
            sh 'mvn test'
            break

        case "node":
            sh 'npm test || true'
            break

        case "python":
            sh 'pytest || true'
            break

        case "go":
            sh 'go test ./...'
            break

        default:
            echo "No Tests"
    }
}
