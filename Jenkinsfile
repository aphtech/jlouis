pipeline {
	agent any
	triggers {
	    cron('0 0 * * * *')
	}
	stages {
		stage("Build") {
			steps {
                sh 'cloc --by-file --xml --out=cloc.xml .'

				sh './gradlew install'
                archiveArtifacts "build/libs/jlouis*jar"

				withMaven(maven: "maven-auto") {
                    sh '''
                    VERSION=`cat .gradle_project_version`
                    cd build/libs
                    mvn deploy:deploy-file \
                        -DgroupId=org.bitbucket.mwhapples \
                        -DartifactId=jlouis \
                        -Dversion=$VERSION \
                        -DrepositoryId=aphtech-mrepo \
                        -Durl=https://nexus.aphtech.org/repository/aphtech-mrepo \
                        -Dfile=jlouis-$VERSION.jar \
                        -Dsources=jlouis-$VERSION-sources.jar \
                        -Djavadoc=jlouis-$VERSION-javadoc.jar \
                        -Dpackaging=jar \
                        -DpomFile=../poms/pom-default.xml \
                        -Dfiles=../distributions/jlouis-$VERSION-tables.zip \
                        -Dclassifiers=tables \
                        -Dtypes=zip \
                        -DupdateReleaseInfo=true
                    '''
                }
			}
            post {
                always {
                    junit testResults: 'build/test-results/test/*.xml', allowEmptyResults: true
                }
            }
		}
	}
}