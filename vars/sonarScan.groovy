def call() {

    withSonarQubeEnv('SonarQube') {

        switch(env.LANG) {

            case "java":
                sh 'mvn sonar:sonar'
                break

            case "node":
                sh 'npx sonar-scanner'
                break

            case "python":
                sh 'sonar-scanner'
                break

            case "go":
                sh 'sonar-scanner'
                break

            default:
                error("Unsupported language")
        }
    }
}
