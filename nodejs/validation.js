const Joi = require('@hapi/joi');

const userRegisterValidation = data => {
    const schema = Joi.object({
      name: Joi.string()
        .min(6)
        .required(),
      email: Joi.string()
        .min(6)
        .required()
        .email(),
      password: Joi.string()
        .min(6)
        .required()
    });
    return schema.validate(data);
  };

  const userLoginValidation = data => {
    const schema = Joi.object({
      email: Joi.string()
        .min(6)
        .required()
        .email(),
      password: Joi.string()
        .min(6)
        .required()
    });
    return schema.validate(data);
  };

  const eventValidation = data => {
    const schema = Joi.object({
      host: Joi.string()
        .min(6)
        .required(),
      title: Joi.string()
        .min(3)
        .required(),
      description: Joi.string()
        .min(6)
        .required(),
      location: Joi.string()
        .min(3)
        .required(),
      eventTimeStamp: Joi.number()
        .required(),
      imageURL: Joi.string()
        .min(6)
        .required(),
      maxAttendees: Joi.number()
        .required(),
      uploadTime: Joi.date()
    });
    return schema.validate(data);
  };

  module.exports.userRegisterValidation = userRegisterValidation;
  module.exports.userLoginValidation = userLoginValidation;
  module.exports.eventValidation = eventValidation;

