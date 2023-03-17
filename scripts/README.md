## Deployment as a service on Oracle Linux / CentOS platform

1. Let's take as an example that we want to setup e-Seal API to run from `/opt/eseal` folder
2. Let's suppose our eSeal API artifact is called `eseal.jar` - the below commands mention this name, e.g. in `eseal.service`
3. prepare a user that will run the service. This user will have to have full ownership of the above folder and ability to create/modify files within it
4. If SYSTEMD / systemctl command is used to manage the service, 
    a. modify `eseal.service`, specifically 
	    * User and Group
		* Environment so that PATH environment variable points to the valid JDK bin folder
	b. copy eseal.service to `/etc/systemd/system/` folder
5. If you setup service using `init.d` (Oracle Linux 6.6), copy `eseal` script according to rough guidelines in `install_service.sh`
6. `chmod +x eseal.jar` 
7. Modify `eseal.conf` to change the default port, using -Dserver.port=XXXX option (8080 is used by default)
