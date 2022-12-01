const path = require("path");

function resolve(dir) {
    return path.join(__dirname, dir);
}

module.exports = {
    productionSourceMap: false,
    devServer: {
        port: 8082,
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
                pathRewrite: {
                    "^/api": ""
                }
            }
        },
        disableHostCheck: true
    },

    chainWebpack(config) {
        // set svg-sprite-loader
        config.module
            .rule('svg')
            .exclude.add(resolve('src/assets/icons'))
            .end()
        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/assets/icons'))
            .end()
            .use('svg-sprite-loader')
            .loader('svg-sprite-loader')
            .options({
                symbolId: 'icon-[name]'
            })
            .end()
    }
};
