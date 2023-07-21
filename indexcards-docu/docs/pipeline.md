# Pipeline

[![indexcard pipeline](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml/badge.svg)](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml) 

The Build Pipeline is executed, if something is pushed to main or on a merge Request.

![Build Pipeline](images/build-pipeline.png)

## Build and Test

The building stage builds the Backend and executes all tests. If there are failures, the pipeline fails and **should not be merged in main**.

## Linting

The linting stage does static code analysis on the indexcard-ui module with [Eslint](https://eslint.org/). 

## Deployment

The deployment stage is executed only when pushed to main and the build and test stage succeeds. This stage is executed on a Self-Hosted Runner, which is a cloud Server. There the running containers are stopped and containers with the new Version are started.

This is a test server and can be reached under [https://indexcard.7ubi.de](https://indexcard.7ubi.de). 

## Deploy documentation

The deploy documentation stage is dependent on the deployment stage. It is also executed on a self-hosted runner. This stage automatically updates the documentation website, which is found here.
