FROM mhart/alpine-node:12.13.0 as base

# Install tools
RUN apk update && apk add openssh && apk add --no-cache git

# Copy project and install packages
COPY . /src
WORKDIR /src

RUN yarn install

# Initialize container
FROM mhart/alpine-node:12.13.0
COPY --from=base /src /src
WORKDIR /src

CMD ["yarn", "start", "local"]