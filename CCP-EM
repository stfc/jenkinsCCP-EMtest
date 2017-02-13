// CCP-EM pipeline

node {
    stage('Load Modules'){
        sh 'module load gcc cmake lapack blas'
    }
    stage('Checkout') {
        sh 'bzr co bzr+http://oisin.rc-harwell.ac.uk/bzr/devtools/trunk devtools\n\
                cd devtools/'
    }
    stage('Download the Deps'){
        sh 'function fail {\n\
                echo $1 >&2\n\
                exit 1\n\
            }\n\
            \n\
            function retry {\n\
                local n=1\n\
                local max=5\n\
                local delay=10\n\
                while true; do\n\
                    "$@" && break || {\n\
                        if [[ $n -lt $max ]]; then\n\
                            ((n++))\n\
                            echo "Command failed. Attempt $n/$max:"\n\
                            sleep $delay;\n\
                        else\n\
                            fail "The command has failed after $n attempts."\n\
                        fi\n\
                    }\n\
                done \n\
            }\n\
            \n\
            retry ./cj --no-interact update bzr setuptools lxml qt4 ccpem >> downloads.log 2>&1'
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