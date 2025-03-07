#  This is used in the Linux instance to clone all the modules from the github
#  Created on:27-01-2025
#  Updated on:27-01-2025
# Created By: Dhananjaya K L (Junior_DevOps_Engineer)
#!/bin/bash

# Prompt for GitHub username and password/token at the start of the script
read -p "Enter your GitHub username: " GIT_USERNAME
read -p "Enter your GitHub password or personal access token: " GIT_PASSWORD
echo ""

# Define available modules globally
availableModules=("grc" "issue" "risk" "controltesting" "businessresilience" 
                  "survey" "admin" "engine" "attendance" "document" 
                  "employee" "incident" "keyshield" "leave" "monitor" 
                  "project" "resolve" "syscare" "vendor" "internalaudit" 
                  "loss" "crm" "za")

# Function to display available modules with proper numbering
function show_available_modules {
    echo -e "\nAvailable modules:"
    local i=1  # Start numbering from 1
    local module_count=${#availableModules[@]}

    for module in "${availableModules[@]}"; do
        if (( i % 5 == 1 )); then
            echo -n "$i.$module"
        else
            echo -n ", $i.$module"
        fi
        if (( i % 5 == 0 || i == module_count )); then
            echo ""
        fi
        i=$((i + 1))
    done
}

# Function to validate modules
function test_modules {
    selectedModules=("$@")
    local validModules=()
    local invalidModules=()

    for module in "${selectedModules[@]}"; do
        if [[ " ${availableModules[*]} " == *" $module "* ]]; then
            validModules+=("$module")
        else
            invalidModules+=("$module")
        fi
    done

    echo "${validModules[@]}|${invalidModules[@]}"
}

# Function to handle module input
function get_selected_modules {
    while true; do
        read -p "Enter module names to process (e.g. grc,issue) or 'All Modules': " inputModules

        if [[ $inputModules == "All Modules" ]]; then
            echo "${availableModules[*]}"
            break
        else
            IFS=',' read -r -a selectedModules <<< "$inputModules"
            selectedModules=("${selectedModules[@],,}")  # Convert to lowercase and trim

            result=$(test_modules "${selectedModules[@]}")
            validModules=$(echo "$result" | cut -d '|' -f 1)
            invalidModules=$(echo "$result" | cut -d '|' -f 2)

            if [ -n "$invalidModules" ]; then
                echo "The following modules are invalid and will be ignored: $invalidModules"
                echo "Please enter the correct module names."
            else
                echo "$validModules"
                break
            fi
        fi
    done
}

# Set paths 
script_location="$(dirname "$0")"
checkoutDir="$script_location/Github"
importDir="$script_location/Modules"
rm -rf $checkoutDir $importDir

mkdir -p "$checkoutDir"
mkdir -p "$importDir"

# Main script execution loop
while true; do
    echo -e "\nSelect the operation to perform:"
    echo "1. Git_Clone"
    read -p "Enter your choice (1): " choice

    case $choice in
        1)
            show_available_modules
            selectedModules=($(get_selected_modules))

            for module in "${selectedModules[@]}"; do
                url="https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/ProGReC-APPS/$module.git"
                moduleImportPath="$importDir/$module"
                moduleCheckoutPath="$checkoutDir/$module"

                mkdir -p "$moduleCheckoutPath"

                # Git operations
                cd "$moduleCheckoutPath"
                git init --initial-branch=development  # Initialize with 'development' as default branch
                git remote add -f origin $url
                git fetch origin

                # Check if the development branch exists; if so, checkout it, otherwise use the default branch
                if git branch -r | grep -q "origin/development"; then
                    git checkout -t origin/development
                else
                    git checkout -t origin/$(git symbolic-ref refs/remotes/origin/HEAD | sed 's@^refs/remotes/origin/@@')
                fi

                # Import Script Logic
                cd ../..
                rm -rf "$moduleImportPath"
                mkdir -p "$moduleImportPath"
                
                cp -r "$moduleCheckoutPath/client/"* "$moduleImportPath"

                echo "Files copied successfully for module: $module"
            done

            # Exit the script after processing all selected modules
            exit 0
            ;;
        *)
            echo "Invalid choice. Please try again."
            ;;
    esac
done
