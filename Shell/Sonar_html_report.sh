# This script is used in he sonarQube pipeline to convert the json to html report.
#  Created on:27-01-2025
#  Updated on:27-01-2025
#  Created By: Dhananjaya K L (Junior_DevOps_Engineer)

#!/bin/bash

# Define date formats
currentDate=$(date +"%Y-%m-%d")
outputHtmlPath="123.html"
jsonFilePath="API.json"

# Export variables to be accessible in Node.js
export currentDate
export outputHtmlPath
export jsonFilePath

# Check if JSON file exists
if [ ! -f "$jsonFilePath" ]; then
    echo "JSON file not found: $jsonFilePath"
    exit 1
fi

# Embedded JavaScript code to process JSON
node - <<'EOF'
const fs = require('fs');

// Load JSON file
const jsonFilePath = process.env.jsonFilePath || 'API.json';
const currentDate = process.env.currentDate || new Date().toISOString().split('T')[0];
let outputHtmlPath = process.env.outputHtmlPath || '123.html';

// Function to generate HTML content
function generateHtmlReport(issues) {
    const sonarQubeUrl = "http://192.168.1.227:9000";
    let htmlContent = `<html>
        <head>
            <title>Scan_Report-${currentDate}</title>
            <style>
                table { width: 100%; border-collapse: collapse; }
                th, td { border: 1px solid black; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
            </style>
        </head>
        <body>
            <h1>Scan_Report-${currentDate}</h1>
            <table>
                <tr>
                    <th>Key</th><th>Rule</th><th>Severity</th><th>Component</th><th>Project</th>
                    <th>Line</th><th>Message</th><th>Author</th><th>Creation Date</th><th>Update Date</th>
                    <th>Status</th><th>Type</th>
                </tr>`;

    for (let issue of issues) {
        const issueUrl = `${sonarQubeUrl}/project/issues?id=${issue.project}&issues=${issue.key}`;
        htmlContent += `<tr>
            <td><a href="${issueUrl}" target="_blank">${issue.key}</a></td>
            <td>${issue.rule}</td><td>${issue.severity}</td><td>${issue.component}</td><td>${issue.project}</td>
            <td>${issue.line || ''}</td><td>${issue.message}</td><td>${issue.author || ''}</td><td>${issue.creationDate}</td>
            <td>${issue.updateDate || ''}</td><td>${issue.status}</td><td>${issue.type}</td>
        </tr>`;
    }

    htmlContent += "</table></body></html>";
    return htmlContent;
}

// Parse JSON data and filter issues
try {
    const jsonData = JSON.parse(fs.readFileSync(jsonFilePath, 'utf-8'));
    const issues = jsonData.issues.filter(issue => issue.creationDate && issue.creationDate.includes(currentDate));

    // Generate and write HTML report
    let htmlContent;
    if (issues.length > 0) {
        htmlContent = generateHtmlReport(issues);
    } else {
        htmlContent = `<html>
            <head><title>Scan_Report-${currentDate}</title></head>
            <body><h1>Scan_Report-${currentDate}</h1>
            <p>There is no new code available to scan.</p>
            </body></html>`;
    }
    fs.writeFileSync(outputHtmlPath, htmlContent);
    console.log(`HTML report written to ${outputHtmlPath}`);
} catch (error) {
    console.error("Error processing JSON data:", error);
    process.exit(1);
}
EOF
