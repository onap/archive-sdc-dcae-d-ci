# Remove all VFCMTs

## Setup
1. Execute dos2unix on all script files: `find . -type f -print0 | xargs -0 dos2unix`
2. Give execute permissions to setup.sh: `chmod +x setup.sh`
3. Run `./setup.sh`

## Usage
Run `run.sh` with the path to the *exportGraph file*

Example: `./run.sh "/var/tmp/exportGraph.1500391226269.json"`
