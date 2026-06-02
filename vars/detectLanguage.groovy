def call() {

    if (fileExists('pom.xml')) {
        env.LANG = 'java'
    }
    else if (fileExists('package.json')) {
        env.LANG = 'node'
    }
    else if (fileExists('requirements.txt')) {
        env.LANG = 'python'
    }
    else if (fileExists('go.mod')) {
        env.LANG = 'go'
    }
    else {
        error("Unsupported project type")
    }

    echo "Detected Language: ${env.LANG}"
}
