// CCP-EM pipeline

def download(){
    sh 'cd devtools/\n\
        pwd\n\
        ./cj --no-interact update bzr setuptools lxml qt4 ccpem >> downloads.log 2>&1'
}
int attempt = 1

node {
    stage('Load Modules'){
        sh 'module load gcc cmake lapack blas'
    }

    stage('Checkout') {
        sh 'bzr co bzr+http://oisin.rc-harwell.ac.uk/bzr/devtools/trunk devtools'
    }
    
    try{
        stage('Download the Deps'){
            download()
        }
    } catch (e) {
        echo "Download failed on attempt " + attempt +": " + e.toString()
        retry(2){
            attempt = attempt++
            echo "Now making attempt " + attempt
            download()
        }        
    }
    
    stage('Build CCP-EM'){
        sh 'cd devtools/\n\
            pwd\n\
            ./cj --no-interact tinderbox -o buildresults bzr setuptools lxml qt4 ccpem'
                
        sh 'cd devtools/\n\
            pwd\n\
            cd install \n\
            tar -czf ccpem_binaries.tar.gz * \n\
            touch ~/.agree2ccp4v6 \n\
            ./CCPEM-BINARY.setup\n\
            source ./bin/ccpem.setup-sh\n\
            cd ../checkout/pytest-xdist-1.14\n\
            ccpem-python setup.py install\n\
            cd ../../\n\
            xvfb-run ccpem-python install/lib/py2/ccpem/unittests/ccpem/run_ccpem_pytest.py -xml=True || true'
    }
}