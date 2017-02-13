// CCP-EM pipeline

node {
    stage('Load Modules'){
        sh 'module load gcc cmake lapack blas'
    }
    stage('Checkout') {
        sh 'bzr co bzr+http://oisin.rc-harwell.ac.uk/bzr/devtools/trunk devtools\n\
                cd devtools/'
    }
}