# sync tool
Synchronization of local files with the related ones at remote server via SSH

  ### Components:
  - Client component:
    - Menu - select target type
    - SyncConfig - configure source, target and connect
    - Pause/Resume - track and manage sync process
  - Core component: implementation of sync algoryth and progress tracking
  - Datasource: base abstractions of directories, local and ssh implementations
  - Monior: tracking source/target directories

## Software Prerequisites (latest versions preferable)
  - Java 11
  - Maven 
  - Intellij Idea

## How to Run the project
  - Run Menu
    - Top Intellij panel -> Run/Debug Configurations -> run "Menu"
    - Set up may take several seconds
    - The app is running now :)
   
## Workflow
 - General workflow regarding the task: user can select target datasource,
  configure it and finally start sync.
 - Target directory is checked once a minute by scheduler, source directory - immediately by WatchService
 - General logic of sync algorythm:
  reestablish directory structure -> delete all unneccessary target files -> copy all needed files from source
 - Arcitecture is built to avoid significant changes while adding new target datasource:
  New datasource should be registred in DatasourceManager(in Menu class)
   and implemnted in datasource package. Minor(or mostly none) UI changes are neccessary.
 - There is integration test for local case: LocalSyncTest. Is it neccessary to test any remote case in automated way?  
 - In case of any questions feel free to ask Me: dyadkinm@gmail.com
