def call() {

    dir(env.APP_DIR ?: '.') {

        switch(env.LANG) {

            case "python":
                sh '''
                python3 -m pip install --upgrade pip
                pip3 install -r requirements.txt
                '''
                break

            case "node":
                sh '''
                npm install
                npm run build || true
                '''
                break

            case "java":
                sh 'mvn clean package -DskipTests'
                break

            case "go":
                sh '''
                go mod tidy
                go build -o app
                '''
                break
        }
    }
}
