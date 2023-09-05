#!/bin/groovy
def imageTag = "1.0.${env.BUILD_NUMBER}"
def artifactPublished = false
def propertiesFileName = "sdp.yaml"
def props
def buildWkspace = ""

pipeline {
    agent any
    tools {
        jdk "JDK 1.17"
    }
    parameters {
        choice(choices: ['EFProduction', 'EFClone', 'EFDevelopment', 'EFEngineering'], description: 'What environment?', name: 'sdpEnvironment')
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: "${env.BRANCH_NAME}" == 'trunk' ? '10' : '5'))
        disableConcurrentBuilds()
        skipDefaultCheckout()
        timestamps()
    }
    stages {
       // Stage #1
       stage('Initialize Pipeline') {
            steps {
                script {
                    properties([
                            pipelineTriggers([[$class: "GitHubPushTrigger"]])
                    ])
                    props = gradle.initPipeline(propertiesFileName, imageTag)
                    echo "sdpEnvironment choice parameter is set to ${params.sdpEnvironment}"
                    props.sdpEnvironment = "$params.sdpEnvironment"
                    echo "Updated properties are " + props
                }
            }
        }
        // Stage #2
        stage('Build Project') {
            steps {
                script {
                    // Build with Gradle Wrapper
                    gradle.configureGradleWithWrapper()
                    gradle.build()
                }
            }
        }
        // Stage #3
        stage('Sonar Analysis') {
            steps {
                script {
                    gradle.sonarAnalysis()
                }
            }
        }
        // Stage #4
        stage('Publish to Artifactory') {
            steps {
                script {
                    imageTag = image.adjustImageTagForBranch(imageTag)
                    boolean isTrunk = (("${env.BRANCH_NAME}".indexOf('trunk')) > 0)
                    boolean isSnapshot = (("${props.versionNumber}".indexOf('-SNAPSHOT')) > 0)
                    println("{'isTrunk':"+isTrunk+"},{'isSnapshot':"+isSnapshot+"}")
                    if ((isTrunk && !isSnapshot) || (!isTrunk && isSnapshot)) {
                        gradle.publishToArtifactory()
                        artifactPublished = true
                    }                     
                }
            }
        }
        // Stage #5
        stage('Create, Scan, and Push Docker Image') {
            agent {
                label 'docker'
            }
            steps {
                script {
                    gradle.createScanAndPushDockerImage("${props.imageName}", imageTag)
                }
            }
        }
        // Stage #6
        stage('Run SDP') {
            steps {
                script {
                    boolean isTriggerDeploy = ("${env.BRANCH_NAME}" == "${props.triggerDeploymentBranch}")
                    println("{'isTriggerDeploy':"+isTriggerDeploy+"}")
                    if (isTriggerDeploy) {
                        gradle.runPipeline(props, imageTag)
                    } else {
                        println("SDP step skipped because the current branch: ${env.BRANCH_NAME} does not match the trigger deployment branch: " +
                                "${props.triggerDeploymentBranch}. Change your Trigger deployment branch in your properties file " +
                                "to match the current branch name if you want the pipeline to run. ")
                    }
                }
            }
        }
    }
    post {
        always {
            junit 'build/test-results/test/*.xml'
            step([$class: 'JacocoPublisher', exclusionPattern: '**/com/**/*'])
        }
        unstable {
            // notify users when the Pipeline is Unstable
            mail to: "${props.onFailureEmail}",
                    subject: "Unstable Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Unstable build: ${env.BUILD_URL}"
        }
        failure {
            // notify users when the Pipeline fails
            mail to: "${props.onFailureEmail}",
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Failure with build: ${env.BUILD_URL}."
        }
    }
}
