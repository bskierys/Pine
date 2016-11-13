#!/bin/bash

msg() {
    echo -e "$@" > /dev/stderr
}

warn() {
    echo -e "${FB}${CY}WARNING:${F0} $@" > /dev/stderr
}

error_exit() {
    echo -e "${FB}${CR}ERROR:${F0} $@" > /dev/stderr
    exit 1
}

check_git_dir() {
    if ! git status --porcelain &> /dev/null ; then
        error_exit "Current dir is not a git repository!"
    fi
}

get_curr_branch() {
    branch=$(git rev-parse --abbrev-ref HEAD)
}

read_version() {
    msg "Reading version from tags..."
    prev=$(git describe | tail -n 1)
}

check_git_dir
get_curr_branch
msg "${branch}"
read_version
msg "${prev}"
git describe

./gradlew bintrayUpload --info
