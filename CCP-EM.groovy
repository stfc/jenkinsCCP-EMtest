// CCP-EM pipeline

def attempt = 1

def download(){
    sh 'cd devtools/\n\
        pwd\n\
        ./cj --no-interact update bzr setuptools lxml qt4 ccpem >> downloads.log 2>&1'
}

node {
    stage('Load Modules'){
        sh 'module load gcc cmake lapack blas'
    }

    stage('Checkout') {
        sh 'bzr co bzr+http://oisin.rc-harwell.ac.uk/bzr/devtools/trunk devtools'
    }
    
    stage('Retry Block'){
        try{
            stage('Download the Deps'){
                try{
                    download()
                } catch (e) {
                    stage('Retry attempts'){
                        echo "Download failed on attempt " + attempt +": " + e.toString()
                        retry(2){
                            attempt++
                            echo "Now making attempt " + attempt
                            download()
                        }
                    }
                }
            }
        }catch (e){
            echo "Failed after retries.\n\
                Error thrown:" + e.toString() + "\n\
                Progressing with build stage."
        }
    }
    
    
    stage('Build CCP-EM'){
        sh 'cd devtools/\n\
            pwd\n\
            ./cj --no-interact tinderbox -o buildresults bzr setuptools lxml qt4 ccpem'
        
        archiveArtifacts artifacts: '**/buildresults/**, **/devtools/install/ccpem_binaries.tar.gz, **/downloads.log', excludes: null
                
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
    archiveArtifacts artifacts: '**/buildresults/**, **/devtools/install/ccpem_binaries.tar.gz, **/downloads.log', excludes: null
}



/**
 * Old block code:
 * 

def downloadStage(attempt){
    stage('Download the Deps')
        try{
            echo "Starting attempt number " + attempt
            download()
        } catch(e){
            echo "Download failed on attempt " + attempt +": " + e.toString()
            attempt++
            if( attempt < 4){
                downloadStage(attempt)
            } else{
                echo "Failed on all " + attempt + " attempts"
            }          
        }
    }
   

stage('Retry Block'){
        try{
            downloadStage(attempt)
        } catch (e){
            echo "Moving on"
        }
    }
*/
