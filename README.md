# Eseal

E-signature Rest API.


Signing, timestamping and validation of PDF documents.

### Code format
```bash
mvn spotless:apply
```


## Funding 
Co-financed by the European Union, the Innovation and Networks Executive Agency and CEF Telecom

Τhis api was developed during the eThemisID project. 

_Full Title_: eThemisID: Integrating the Greek Justice System with eIDAS and e-signature services

_Duration_: 01/01/2020- 31/07/2021 

_Project ID_: [2019-el-ia-0026](https://ec.europa.eu/inea/en/connecting-europe-facility/cef-telecom/2019-el-ia-0026)

_Results can be found_: [here](https://www.adjustice.gr/ethemis/?lang=en) 

## Deployment as a service on Oracle Linux / CentOS platform

1. Suppose that we want to setup e-Seal API to run from `/opt/eseal` folder
2. After successful build, 
    a. rename `target/eseal-1.0.jar` to `eseal.jar` - from now on suppose that eSeal API artifact is called `eseal.jar`, e.g. in `eseal.service`
	b. copy `eseal.jar`, `src/main/resources/logback.xml`, `scripts/eseal.conf` to `/opt/eseal` folder. The latter files are used to externalize the configuration.
	c. optionally create `application.properties` in `/opt/eseal` folder to be able to override the default `src/main/resources/application.properties`
3. Prepare a system user that will run the service. This user will have to have full ownership of the above folder and ability to create/modify files within it
4. If `systemd` is used to manage the service, 
    a. modify `scripts/eseal.service`, specifically 
	    * User and Group
		* Environment so that PATH environment variable points to the valid JDK bin folder
	b. copy the above `eseal.service` to `/etc/systemd/system/` folder
	c. run `systemctl daemon-reload` to refresh systemd configuration - after that use `sudo service eseal start|restart|stop` commands to control the execution, or `sudo systemctl start|restart|stop eseal`
5. If service is setup using `init.d` (e.g. in Oracle Linux 6.6), use `scripts/eseal` script:
    a. modify the above `eseal` script to point to the right path for logging (`/var/log/eseal` is referenced by default and should be created if left like this)
	b. make sure there are no carriage return characters (Windows line endings) in the file
	c. copy `eseal` to `/etc/init.d/`, run `chmod +x eseal` to make it executable
	d. run `chkconfig --add eseal` followed by `chkconfig --level 2345 eseal on` to enable the service
6. Make sure `eseal.jar` is marked as executable, i.e. execute `chmod +x /opt/eseal/eseal.jar` 
7. Optionally modify `/opt/eseal/eseal.conf` to change the default port, using -Dserver.port=XXXX option (8080 is used by default)