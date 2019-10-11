const JsonWebToken = require('jsonwebtoken');

module.exports = function authentication (req,res,next){
    const token = req.header('auth-token');
    if(!token) return res.status(401).send('Access Denied');
    try{
        const verified = JsonWebToken.verify(token,process.env.TOKEN_SECRET);
        req.user = verified;
        next();
    }catch(err){
        res.send('401:Invalid Token');
    }
}