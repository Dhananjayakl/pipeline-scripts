def call() {
    script {
        echo "Cloning repository based on Module_Name parameter..."
        def repoUrl

        // Determine which repository URL to use based on Module_Name parameter
        switch (params.Module_Name) {
            case 'grc':
                repoUrl = env.grc_repo
                break
            case 'issue':
                repoUrl = env.issue_repo
                break
            case 'risk':
                repoUrl = env.risk_repo
                break
            case 'controltesting':
                repoUrl = env.controltesting_repo
                break
            case 'businessresilience':
                repoUrl = env.businessresilience_repo
                break
            case 'survey':
                repoUrl = env.survey_repo
                break
            case 'admin':
                repoUrl = env.admin_repo
                break
            case 'engine':
                repoUrl = env.engine_repo
                break    
            case 'attendance':
                repoUrl = env.attendance_repo
                break
            case 'documentmanagment':
                repoUrl = env.documentmanagment_repo
                break
            case 'employee':
                repoUrl = env.employee_repo
                break
            case 'incident':
                repoUrl = env.incident_repo
                break
            case 'keyshield':
                repoUrl = env.keyshield_repo
                break
            case 'leave':
                repoUrl = env.leave_repo
                break    
            case 'monitor':
                repoUrl = env.monitor_repo
                break
            case 'project':
                repoUrl = env.project_repo
                break
            case 'resolve':
                repoUrl = env.resolve_repo
                break
            case 'syscare':
                repoUrl = env.syscare_repo
                break
            case 'vendor':
                repoUrl = env.vendor_repo
                break
            case 'internalaudit':
                repoUrl = env.internalaudit_repo
                break
            case 'loss':
                repoUrl = env.loss_repo
                break
            case 'crm':
                repoUrl = env.crm_repo
                break
            case 'za':
                repoUrl = env.za_repo
                break                                                                         
            default:
                error "Unsupported Module_Name parameter value: ${params.Module_Name}"
        }

        // Clone the selected repository
        bat "git clone ${repoUrl}"
    }
}
