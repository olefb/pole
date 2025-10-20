# DAT250: Software Technology Experiment Assignment 7

https://github.com/olefb/pole

## Summary

This assignment involved writing Docker configuration files for the project. I used [Patrick's example file](https://github.com/selabhvl/dat250public/blob/master/lectureexamples/l15_containers/Dockerfile) as the template, but quickly realised I also needed separate containers for the Valkey and RabbitMQ services. I glued these together with a Docker Compose file that ensures the Spring project depends on the Valkey and RabbitMQ containers. I then added the step to copy a freshly built frontend into the backend. Finally, I added Dependabot to the Github repository in order to get updated dependencies.

`docker-compose.yml` is in the project root. `Dockerfile` is located in the `backend` directory.

The container was tested by running `sudo docker-compose up --build`.

## Technical issues encountered

No big issues were encountered. I had to experiment a bit with the paths and COPY statements configured in the Docker files for it to work. The Valkey and RabbitMQ dependency issue was mentioned above.