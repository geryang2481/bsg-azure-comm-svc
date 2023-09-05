@Library(['com.optum.jenkins.pipeline.library', 'ecp-shared-jenkins-library']) _

// build with Java 11 instead of the default Java 8
def defaults = [
        javaVersion : '11.0'
]

def config = merge(params, defaults)
clContinuousBuild(config)
