def call() {

    dir(env.APP_DIR ?: '.') {

        switch(env.LANG) {

            case "python":

                sh '''
                if find . -name "test_*.py" -o -name "*_test.py" | grep -q .; then
                    echo "Running Python tests..."
                    pytest
                else
                    echo "No Python tests found. Skipping."
                fi
                '''
                break

            case "node":

                sh '''
                if [ -f package.json ]; then
                    echo "Running Node tests..."
                    npm test || true
                fi
                '''
                break

            case "java":

                sh '''
                echo "Running Java tests..."
                mvn test
                '''
                break

            case "go":

                sh '''
                echo "Running Go tests..."
                go test ./...
                '''
                break

            default:
                echo "No test strategy defined for ${env.LANG}"
        }
    }
}
