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
            python3 -m venv venv
            . venv/bin/activate
            pip install --upgrade pip
            pip install -r requirements.txt
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
