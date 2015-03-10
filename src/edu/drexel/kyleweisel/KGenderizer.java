package edu.drexel.kyleweisel;

import org.json.JSONObject;

import java.net.MalformedURLException;

public class KGenderizer {

    public final String getGender(final String name) {

        try {

            final SimpleRESTClient client = new SimpleRESTClient();
            final ResponseWrapper rw = client.makeRequest("https://api.genderize.io/?name=" + name);

            if (!rw.isError()) {
                System.out.println("The response was: " + rw.response);

                JSONObject response = new JSONObject(rw.response);

                final String gender = response.optString("gender");
                final double probability = response.optDouble("probability");

                return gender;

            }

        }
        catch (MalformedURLException exception) {
            System.out.println("getGender() -> Caught MalformedURLException.  It said: " + exception.getMessage());
        }

        return null;

    }

}
