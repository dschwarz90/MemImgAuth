# MemImgAuth
MemImgAuth stands for `Memory Image Authentication` and is a recognition-based authentication scheme for mobile devices. It is using a user's self-taken photographs for the authentication process. 

MemImgAuth is a multi-user system. The users can create and delete their own account. Since this system is only a prototype application, the accounts are not secured. After logging in, the users select an image set, consisting of n photos. Out of these n photos, the users select 4 pass images. These pass images are later used as a password. After this, a so called Key Pass Image must be chosen. The Key Pass Image is an image, that will be selected as the first image in every authentication session. The remaining images are used as decoy images. A decoy images is not used for authentication, but obfuscates the pass images.

In the authentication process, the decoy images and pass images will displayed in a random order in an image grid. MemImgAuth displays 20 images per page. When the users successfully select the Key Pass Image and the pass images, they can authenticate on the system. The Key Pass Image must be selected as the first image. The selection order of the remaining key images doesn't matter. 
When the selection of both Key Pass Image and pass images is finished, the users will see a result screen with the operation time for the authentication session.

## About this Project
This proof of concept application originates from a research project that I carried out during my research year at the University of Electro-Communications in Tokyo, Japan in the year 2016/2017. It is related to the field of Usable IT Security. The research question was, if the concept of the Key Pass Image could have positive influence on both theoretical security and usability, since we wanted to reach a better balance point between security and usability without bringing an extra burden for the user. During this project, we conducted a user experiment with 3 different authentication conditions.

**THIS PROJECT IS NOT LONGER MAINTAINED**
