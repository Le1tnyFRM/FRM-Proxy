# FRM Proxy
Standalone proxy which allows players to join EVERY Minecraft server version (Classic, Alpha, Beta, Release, Bedrock)

To download the latest version, go to the [Releases section](#executable-jar-file) and download the latest version.  
Using it is very simple, just run the jar file, and it will start a user interface where everything can be configured.  
For a full user guide go to the [Usage for Players](#usage-for-players-gui) section or the [Usage for Server Owners](#usage-for-server-owners-config) section.

## Supported Server versions
- Release (1.0.0 - 26.2)
- Beta (b1.0 - b1.8.1)
- Alpha (a1.0.15 - a1.2.6)
- Classic (c0.0.15 - c0.30 including [CPE](https://wiki.vg/Classic_Protocol_Extension))
- April Fools (3D Shareware, 20w14infinite, 25w14craftmine)
- Combat Snapshots (Combat Test 8c)
- Bedrock Edition 1.26.30 (WIP, many things are missing and we are planing on stopping bedrock updates)

## Supported Client versions
- Release (1.7.2 - 26.2)
- April Fools (3D Shareware, 25w14craftmine)
- Bedrock Edition (Requires the [Geyser plugin](https://geysermc.org/download))
- Beta 1.7.3 (Requires the [Beta2Release plugin](https://github.com/ViaVersionAddons/ViaProxyBeta2Release))
- Classic, Alpha, Beta, Release 1.0 - 1.6.4 (Only passthrough)

FRM Proxy supports joining to any of the listed server version from any of the listed client versions.

## Special Features
- Support for joining online mode servers
- Support for joining on servers which have chat signing enabled from all listed client versions
- Supports transfer and cookies for <=1.20.4 clients on 1.20.5+ servers
- Supports Simple Voice Chat mod
- Fixed `Badly compressed packet` for 6b6t / anarchy servers (1.1.2)

## Releases
### Executable Jar File
If you want the executable jar file you can download a stable release from [GitHub Releases](https://github.com/Le1tnyFRM/FRM-Proxy/releases/latest) or the latest dev version from [GitHub Actions](https://github.com/Le1tnyFRM/FRM-Proxy/actions/workflows/build.yml).

### How to use FRM-Proxy
Tutorial: https://youtu.be/oKGCrDZkbCQ

## Usage for Players (GUI)
1. Download the latest version from the [Releases section](#executable-jar-file)
2. Put the jar file into a folder (FRM Proxy will generate config files and store some data there)
3. Run the jar file (`java -jar FRM-Proxy-1.1.2.jar`)
4. Fill in the required fields like server address and version
5. If you want to join online mode servers, add your Minecraft account in the Accounts tab
6. Click on "Start"
7. Join with your Minecraft client on the displayed address
8. Have fun!

## Usage for Server owners (Config)
1. Download the latest version from the [Releases section](#executable-jar-file)
2. Put the jar file into a folder
3. Run the jar file (Using `java -jar FRM-Proxy-1.1.2.jar config viaproxy.yml`)
4. FRM Proxy now generates a config file called `viaproxy.yml` in the same folder and exits
5. Open the config file and configure the proxy (Most important options are at the top)
6. Start the proxy using the start command and test whether it works
7. Have fun!

## Usage for Server owners (CLI)
1. Download the latest version from the [Releases section](#executable-jar-file)
2. Put the jar file into a folder
3. Run the jar file (Using `java -jar FRM-Proxy-1.1.2.jar cli --help`)
4. FRM Proxy will print the CLI usage and exit
5. Configure the proxy and optionally put the finished start command into a script
6. Start the proxy and test whether it works
7. Have fun!

## Credits
Main dev: **Le1tny** | Helped: **aagaming22**  
Based on ViaProxy by RK_01/RaphiMC & Lenni0451 

## Contact
If you encounter any issues, please report them on the [issue page](https://github.com/Le1tnyFRM/FRM-Proxy/issues).  
Discord: https://discord.gg/JDnqyqWm43
