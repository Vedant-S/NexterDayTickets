const router = require('express').Router();
const User = require('../model/User');
const JsonWebToken = require('jsonwebtoken');
const bcrpt = require('bcrypt');
const {userRegisterValidation,userLoginValidation} = require('../validation');

//Register
router.post('/register', async (req,res) => {

    //Let's validate the data before we make the user
    const { error } = userRegisterValidation(req.body);
    if (error) return res.send(error.details[0].message);

    //To check whether the user is already in the database
    const emailExist = await User.findOne({email: req.body.email});
    if(emailExist)  return res.send('Email already exists');

    //Hash Password
    const salt = await bcrpt.genSalt(10);
    const hashedPassword = await bcrpt.hash(req.body.password,salt);

    //Creating a new user
    const user = new User({
        name: req.body.name,
        email: req.body.email.toLowerCase(),
        password: hashedPassword
    });

    //Try & Catch for asynchronous task
    try{
        const savedUser = await user.save();
        res.send("SUCCESS:"+{user: user.id});
    } catch (err){
        res.send(err);
    } 
});

//Login
router.post('/login', async (req,res) => {
    //Let's validate the data before we login
    const { error } = userLoginValidation(req.body);
    if (error) return res.send(error.details[0].message);

    //To check whether the email is in the database
    const user = await User.findOne({email: req.body.email});
    if(!user)  return res.send('Email does not exist.');

    //Password is correct
    const validPassword = await bcrpt.compare(req.body.password.toLowerCase(),user.password);
    if(!validPassword) return res.send('Invalid password');

    //Create & Assign Token
    const userToken = JsonWebToken.sign({ _id: user._id,name:user.name,email:user.email }, process.env.TOKEN_SECRET);
    res.header('auth-token',userToken).send("SUCCESS:"+userToken+";"+user.email+";"+user.name);
});

module.exports = router;