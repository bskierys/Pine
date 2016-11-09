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

check_if_release() {
    get_curr_branch
    if ! echo $branch | grep -E 'release/.*' > /dev/null; then
        error_exit "$branch is not a release branch"  
    fi
}

get_curr_version() {
	get_curr_branch
	version=${branch#*"release/"}
}

replace_version() {
    sed -r -e "s/($2\s*\=\s*)[0-9]+/\1$3/" -i $1
}

set_full_version(){
	IFS='.' read -ra ADDR <<< "$version"

	for ((i=0; i<3; i++));
	do
		if [[ ${ADDR[$i]} = "" ]]
			then ADDR[$i]=0
		fi
	done

	versionMajor="${ADDR[0]}"
	versionMinor="${ADDR[1]}"
	versionPatch="${ADDR[2]}"

	replace_version gradle.properties "versionMajor" $versionMajor
	replace_version gradle.properties "versionMinor" $versionMinor
	replace_version gradle.properties "versionPatch" $versionPatch
}

check_git_dir
check_if_release
get_curr_version
msg "Updating project version to: ${version}"
set_full_version


