# egen-be-challenge
Egen Rest API challenge

URLs for the methods:
  GET: http://localhost:4567/users
        Returns status code 200.
        
  POST: http://localhost:4567/users
        Returns status code 201 if creation successful.
        Returns code 409 if user with emailId alredy exists.
        
  PUT: http://localhost:4567/users/<userId>
        Returns 204 if updation is successful.
        Returns 404 if userId does not exist.

All methods are factored to work with a JSON request body.



