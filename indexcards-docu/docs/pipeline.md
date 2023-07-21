# Pipeline

## Build Pipeline

[![indexcard pipeline](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml/badge.svg)](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml) 

The Build Pipeline is executed, if something is pushed to main or on a merge Request.

![Build Pipeline](images/build-pipeline.png)

### Building Stage

The building stage builds the Backend and executes all tests. If there are failures, the pipeline fails and **should not be merged in main**.