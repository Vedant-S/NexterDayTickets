const router =  require('express').Router();
const verify = require('../verifyToken');
const Event = require('../model/Event');
const {eventValidation} = require('../validation');

router.post('/add',verify,async (req,res) => {

     //Let's validate the data before we add the user
     const { error } = eventValidation(req.body);
     if (error) return res.send(error.details[0].message);

     //Creating a new user
     const event = new Event({
        host: req.body.host,
        title: req.body.title,
        description: req.body.description,
        location: req.body.location,
        eventTimeStamp: req.body.eventTimeStamp,
        imageURL: req.body.imageURL,
        maxAttendees: req.body.maxAttendees
     });
 
     //Try & Catch for asynchronous task
     try{
         const savedEvent = await event.save();
         res.send("EVENT_ADDED");
     } catch (err){
         res.send(err);
     } 
    res.send(req.event);
});

router.get('/all',verify,async (req,res) => {

    //Try & Catch for asynchronous task
    try{
        const allEvents = await Event.find({})
            res.send(allEvents);
    } catch (err){
        res.send(err);
    } 
});


router.post('/search',verify,async (req,res) => {

    //Try & Catch for asynchronous task
    try{
        const searchedEvent = await Event.findOne({_id:req.body.eventUID})
        if(searchedEvent.) return res.send(searchedEvent);
        return res.send("NO_EVENT_EXISTS");
    } catch (err){
        res.send(err);
    } 
});

module.exports = router;
