def call() {

    switch(env.LANG) {

        case "python":

            def pythonRoot = "."

            if (fileExists("Application-Code/requirements.txt")) {
                pythonRoot = "Application-Code"
            }

            sh """
                docker run --rm \
                  -v ${env.WORKSPACE}:/app \
                  -w /app/${pythonRoot} \
                  python:3.12 \
                  sh -c '
                    pip install -r requirements.txt

                    if find . -name "test_*.py" -o -name "*_test.py" | grep -q .; then
                        echo "Running Python tests..."
                        pytest -v
                    else
                        echo "No Python tests found. Skipping."
                    fi
                  '
            """
            break


        case "java":

            sh 'mvn test'
            break


        case "node":

            sh 'npm test || true'
            break


        case "go":

            sh 'go test ./...'
            break


        default:

            echo "No tests configured for ${env.LANG}"
    }
}
