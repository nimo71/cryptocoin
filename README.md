# Cryptocoin

A [re-frame](https://github.com/Day8/re-frame) application designed to observe the poloniex 
cryptocoin exchange. 

## Development Mode

### Compile css:

Compile css file once.

```
lein garden once
```

Automatically recompile css file on change.

```
lein garden auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

```
lein clean
lein cljsbuild auto test
```

## Production Build

```
lein clean
lein cljsbuild once min
```

###Node Clojurescript 

Terminal 1...

> npm install ws

> npm install autobahn

> lein figwheel server-dev


Terminal 2...

> node target/server_out/cryptocoin.js
