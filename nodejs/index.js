const express = require('express');
const app = express();

//Using dotenv to protect our username & password when we push this to GITHUB or HEROKU
const dotenv = require('dotenv');
const mongoose = require('mongoose');

//Import routes
const authRoute = require('./routes/authentication');
const eventRoute = require('./routes/events');
const validateRoute = require('./routes/validateToken');


dotenv.config();

//Connect to MongoDB
mongoose.connect(
    process.env.DB_CONNECT,
    {useNewUrlParser: true, useUnifiedTopology: true},    
    () => console.log('Connected to mongoDB'));

//Middlewares

//For Post Request [Body Parser]
app.use(express.json());

//Routes middleware (Prefix)
app.use('/api/user',authRoute);
app.use('/api/events',eventRoute);
app.use('/api/validate',validateRoute);
app.use('/api/test',(req,res) => {
    res.send('Server Up & Running');
});

app.listen(3000, () => console.log('Server running'));