// CCP-EM pipeline

node {
    stage('Load Modules'){
        sh 'module load gcc cmake lapack blas'
    }
    stage('Checkout') {
        sh 'bzr co bzr+http://oisin.rc-harwell.ac.uk/bzr/devtools/trunk devtools'
    }
    stage('Download the Deps'){
        sh 'cd devtools/\n\
            ./cj --no-interact update bzr setuptools lxml qt4 ccpem >> downloads.log 2>&1'
    }
    stage('Build CCP-EM'){
        sh './cj --no-interact tinderbox -o buildresults bzr setuptools lxml qt4 ccpem'
                
        sh 'cd install \n\
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