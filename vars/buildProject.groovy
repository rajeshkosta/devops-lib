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

        def call() {

    // detect python project location
    def pythonRoot = ""

    if (fileExists("requirements.txt")) {
        pythonRoot = "."
    } 
    else if (fileExists("Application-Code/requirements.txt")) {
        pythonRoot = "Application-Code"
    } 
    else {
        error "requirements.txt not found"
    }

    echo "Python project root: ${pythonRoot}"

    sh """
    docker run --rm \
    -v \$PWD:/app \
    -w /app/${pythonRoot} \
    python:3.12 \
    sh -c "pip install -r requirements.txt && pytest"
    """
}

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
