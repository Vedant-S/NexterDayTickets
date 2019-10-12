const mongoose = require('mongoose');

const eventSchema  = new mongoose.Schema({
    host:{
        type: String,
        required: true,
        min: 6,
        max: 255
    },
    title:{
        type: String,
        required: true,
        min: 3,
        max: 255
    },
    description:{
        type: String,
        required: true,
        min: 6
    },
    location:{
        type: String,
        required: true,
        min: 3
    },
    eventTimeStamp:{
        type: Number,
        required: true,
        min: 6
    },
    imageURL:{
        type: String,
        required: true,
        min: 6
    },
    maxAttendees:{
        type: Number,
        required: true,
    },
    uploadTime:{
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Event',eventSchema);