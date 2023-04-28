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

## Build process
There is one particular integration testcase which requires TLS 1.3 and will fail when built by older versions of Java 8. E.g. build is successful when using AdoptOpenJDK 1.8_362.

## Deployment as a service

The following guidelines were made with specifically Oracle Linux and CentOS in mind.

1. Suppose that we want to setup e-Seal API to run from `/opt/eseal` folder
2. After successful build
   * rename `target/eseal-1.0.jar` to `eseal.jar` - from now on suppose that eSeal API artifact is called `eseal.jar`, e.g. in `eseal.service`
   * copy `eseal.jar`, `src/main/resources/logback.xml`, `scripts/eseal.conf` to `/opt/eseal` folder. The latter files are used to externalize the configuration
   * optionally create `application.properties` in `/opt/eseal` folder to be able to override the default `src/main/resources/application.properties`
   * note that the log levels defined in application.properties will have to work in conjunction with the levels defined in logback.xml - check them both if you don't want to surprise yourself
3. Prepare a system user that will run the service. This user will have to have full ownership of the above folder and ability to create/modify files within it
4. If `systemd` is used to manage the service, 
   * modify `scripts/eseal.service`, specifically
     + User and Group
     + Environment so that PATH environment variable points to the valid JDK bin folder
   * copy the above `eseal.service` to `/etc/systemd/system/` folder
   * run `systemctl daemon-reload` to refresh systemd configuration - after that use `sudo service eseal start|restart|stop` commands to control the execution, or `sudo systemctl start|restart|stop eseal`
5. If service is setup using `init.d` (e.g. in Oracle Linux 6.6), use `scripts/eseal` script:
   * modify the above `eseal` script to point to the right path for logging (`/var/log/eseal` is referenced by default and should be created if left like this)
   * make sure there are no carriage return characters (Windows line endings) in the file
   * copy `eseal` to `/etc/init.d/`, run `chmod +x eseal` to make it executable
   * run `chkconfig --add eseal` followed by `chkconfig --level 2345 eseal on` to enable the service
6. Make sure `eseal.jar` is marked as executable, i.e. execute `chmod +x /opt/eseal/eseal.jar` 
7. Optionally modify `/opt/eseal/eseal.conf` to change the default port, using `-Dserver.port=XXXX` option (8080 is used by default)
8. Make sure that the current date on the server is right - possibly setup the `NTP` service (`ntpd`) because before sending request to HARICA, eSeal API generates a dynamic OTP that is time-based e.g. 
   * `sudo yum install ntp` 
   * `sudo service ntpd start`
   * `sudo chkconfig ntpd on`
8. Use `sudo service eseal start|stop|restart` to control the service
