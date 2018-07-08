#version 150
//#extension GL_ARB_shading_language_420pack : require
#extension GL_ARB_explicit_attrib_location : require

#define TASK 10
#define ENABLE_OPACITY_CORRECTION 0
#define ENABLE_LIGHTNING 0
#define ENABLE_SHADOWING 0
#define FRONT_TO_BACK 1

in vec3 ray_entry_position;

layout(location = 0) out vec4 FragColor;

uniform mat4 Modelview;

uniform sampler3D volume_texture;
uniform sampler2D transfer_texture;


uniform vec3    camera_location;
uniform float   sampling_distance;
uniform float   sampling_distance_ref;
uniform float   iso_value;
uniform vec3    max_bounds;
uniform ivec3   volume_dimensions;

uniform vec3    light_position;
uniform vec3    light_ambient_color;
uniform vec3    light_diffuse_color;
uniform vec3    light_specular_color;
uniform float   light_ref_coef;

float e = 0.001;


bool
inside_volume_bounds(const in vec3 sampling_position)
{
    return (   all(greaterThanEqual(sampling_position, vec3(0.0)))
            && all(lessThanEqual(sampling_position, max_bounds)));
}


float
get_sample_data(vec3 in_sampling_pos)
{
    vec3 obj_to_tex = vec3(1.0) / max_bounds;
    return texture(volume_texture, in_sampling_pos * obj_to_tex).r;

}

vec3 
binary_search(vec3 previous_pos, vec3 sampling_pos)
{   
    vec3 new_sampling_pos = sampling_pos;
    for(int i = 0; i<=100; i++){
        if (i == 100){
            return sampling_pos;
        }else{
            vec3 mid_pos = (previous_pos + (new_sampling_pos - previous_pos)/2);
            float s = get_sample_data(mid_pos);
            if (abs(s-iso_value) < e){
                return mid_pos;
            } else if (s>iso_value){
                new_sampling_pos = mid_pos;
            }
            else{
                previous_pos = mid_pos;

            }
        }
    }
}

vec3
get_gradient(vec3 sampling_pos)
{
    float step_x = max_bounds.x/volume_dimensions.x;
    float step_y = max_bounds.y/volume_dimensions.y;
    float step_z = max_bounds.z/volume_dimensions.z;


    float x = sampling_pos.x;
    float y = sampling_pos.y; 
    float z = sampling_pos.z;

    float dx = (get_sample_data(vec3(x+ step_x, y, z))-get_sample_data(vec3(x-step_x,y,z)))/2;
    float dy = (get_sample_data(vec3(x, y+step_y, z))-get_sample_data(vec3(x,y-step_y,z)))/2;
    float dz = (get_sample_data(vec3(x, y, z+step_z))-get_sample_data(vec3(x,y,z-step_z)))/2;

    return vec3(dx, dy, dz);
}

void main()
{
    /// One step trough the volume
    vec3 ray_increment      = normalize(ray_entry_position - camera_location) * sampling_distance;
    /// Position in Volume
    vec3 sampling_pos       = ray_entry_position + ray_increment; // test, increment just to be sure we are in the volume

    /// Init color of fragment
    vec4 dst = vec4(0.0, 0.0, 0.0, 0.0);

    /// check if we are inside volume
    bool inside_volume = inside_volume_bounds(sampling_pos);
    
    if (!inside_volume)
        discard;

#if TASK == 10
    vec4 max_val = vec4(0.0, 0.0, 0.0, 0.0);
    
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume) 
    {      
        // get sample
        float s = get_sample_data(sampling_pos);
                
        // apply the transfer functions to retrieve color and opacity
        vec4 color = texture(transfer_texture, vec2(s, s));
           
        // this is the example for maximum intensity projection
        max_val.r = max(color.r, max_val.r);
        max_val.g = max(color.g, max_val.g);
        max_val.b = max(color.b, max_val.b);
        max_val.a = max(color.a, max_val.a);
        
        // increment the ray sampling position
        sampling_pos  += ray_increment;

        // update the loop termination condition
        inside_volume  = inside_volume_bounds(sampling_pos);
    }

    dst = max_val;
#endif 
    
#if TASK == 11
    vec4 sum_val = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 avg_val = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 grad_val = vec4(0.0, 0.0, 0.0, 0.0);
    int counter = 0;
    float fac = 3.0;

    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    
    while (inside_volume)
    {      
        counter++;
        // get sample
        float s = get_sample_data(sampling_pos);

        // apply the transfer functions to retrieve color and opacity
        vec4 color = texture(transfer_texture, vec2(s, s));

        // dummy code
        sum_val.r = sum_val.r+color.r;
        sum_val.g = sum_val.g+color.g;
        sum_val.b = sum_val.b+color.b;
        sum_val.a = sum_val.b+color.a;

        avg_val.r = (sum_val.r/counter)*fac;
        avg_val.g = (sum_val.g/counter)*fac;
        avg_val.b = (sum_val.b/counter)*fac;
        avg_val.a = (sum_val.b/counter)*fac;

        
        // increment the ray sampling position
        sampling_pos  += ray_increment;

        // update the loop termination condition
        inside_volume  = inside_volume_bounds(sampling_pos);
    }
    dst = avg_val;




#endif
    
#if TASK == 12 || TASK == 13
vec4 grad_val = vec4(0.0, 0.0, 0.0, 0.0);
    vec3 previous_pos = sampling_pos;
    float previous_s = 0;

    vec3 test = get_gradient(sampling_pos);
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added
    while (inside_volume)
    {
        vec3 new_pos = sampling_pos;
        // get sample
        float s = get_sample_data(new_pos);


        if (sign(s-iso_value) != sign(previous_s-iso_value)) {
            if (TASK==13){
                new_pos = binary_search(previous_pos, sampling_pos);
                s = get_sample_data(new_pos);
            }

        vec4 color = texture(transfer_texture, vec2(s, s));
        vec4 light = vec4(0.0,0.0,0.0,1.0);
        dst = color;
        
        

      
/*#if TASK == 13// Binary Search
        IMPLEMENT
#endif*/


#if ENABLE_LIGHTNING == 1 // Add Shading
    vec3 normal = normalize(get_gradient(new_pos)); 
    vec3 light_direction = normalize(light_position-new_pos);

    float cosTheta = clamp(dot(normal, light_direction), 0, 1); 
    

    vec3 eye = normalize(camera_location-new_pos);
    vec3 reflect = (-light_direction,normal); 

    float cosAlpha = clamp(dot(eye,reflect), 0, 1);

    float light_power = 7;

    float visibility = 1.0;

    
#if ENABLE_SHADOWING == 1 // Add Shadows
    
    /// One step trough the volume
    vec3 ray_shadow_increment      = normalize(new_pos - light_position) * sampling_distance;
    /// Position in Volume
    vec3 shadow_sampling_pos       = new_pos + ray_shadow_increment; // test, increment just to be sure we are in the volume

    bool inside_shadow_volume = inside_volume_bounds(shadow_sampling_pos);

    while(inside_shadow_volume){
        float s_shadow = get_sample_data(shadow_sampling_pos); 

        if(s_shadow>=iso_value)
        {
            visibility = 0.0;
        }
        shadow_sampling_pos += ray_shadow_increment; 
        inside_shadow_volume =inside_volume_bounds(shadow_sampling_pos);
    }

#endif
    light.rgb = light_ambient_color + 
                light_diffuse_color * light_power * cosTheta * visibility+ 
                light_specular_color * light_power * pow(cosAlpha, light_ref_coef) * visibility;

    dst = color*light;
#endif

previous_s = s;


break;
}
        // increment the ray sampling position
        sampling_pos += ray_increment;
        // update the loop termination condition
        inside_volume = inside_volume_bounds(sampling_pos);
    }
#endif 

#if TASK == 31
    // the traversal loop,
    // termination when the sampling position is outside volume boundarys
    // another termination condition for early ray termination is added

    float s = get_sample_data(sampling_pos);
    float trans = 1.0;
    float trans_prev = 1.0;
    vec3 inten = vec3(0.0,0.0,0.0);
    
    vec3 last_pos = vec3(0,0,0);
    bool found_last_position = false;
    bool inside_volume_2 = true;
    

    #if FRONT_TO_BACK == 1
        while(inside_volume){
            s = get_sample_data(sampling_pos);
            vec4 color = texture(transfer_texture, vec2(s, s));
            float opacity = color.a; 
            if (ENABLE_OPACITY_CORRECTION == 1){
              opacity = (1-pow((1-opacity), (sampling_distance_ref/sampling_distance)));
            }
            if(trans > 0.1){
                trans *= trans_prev; 
                inten = inten + trans * color.rgb * opacity;
            }
        
            else{

                break;
            }

            vec4 light = vec4(0.0,0.0,0.0,1.0);

            if (ENABLE_LIGHTNING == 1){

                vec3 normal = normalize(get_gradient(sampling_pos)); 
                vec3 light_direction = normalize(light_position-sampling_pos);

                float cosTheta = clamp(dot(normal, light_direction), 0, 1); 

                vec3 eye = normalize(camera_location-sampling_pos);
                vec3 reflect = (-light_direction,normal); 

                float cosAlpha = clamp(dot(eye,reflect), 0, 1);

                float light_power = 7;
                float visibility = 1.0;

                /*

                if (ENABLE_SHADOWING == 1){// Add Shadows
    
                    /// One step trough the volume
                    vec3 ray_shadow_increment      = normalize(sampling_pos - light_position) * sampling_distance;
                    /// Position in Volume
                    vec3 shadow_sampling_pos       = sampling_pos + ray_shadow_increment; // test, increment just to be sure we are in the volume
                
                    bool inside_shadow_volume = inside_volume_bounds(shadow_sampling_pos);
                
                    while(inside_shadow_volume){
                        float s_shadow = get_sample_data(shadow_sampling_pos); 
                
                        if(s_shadow>=iso_value)
                        {
                            visibility = 0.0;
                        }
                        shadow_sampling_pos += ray_shadow_increment; 
                        inside_shadow_volume =inside_volume_bounds(shadow_sampling_pos);
                    }
                } 
                */

                light.rgb = light_ambient_color + 
                light_diffuse_color * light_power * cosTheta * visibility + 
                light_specular_color * light_power * pow(cosAlpha, light_ref_coef) * visibility;
                dst = vec4(inten.r, inten.g, inten.b, 1-trans) * light;
            }
            else{
                dst = vec4(inten.r, inten.g, inten.b, 1-trans);
            }

            // increment the ray sampling position
            sampling_pos += ray_increment;
            trans_prev = 1-opacity;
            // update the loop termination condition
            inside_volume = inside_volume_bounds(sampling_pos);
        }
    #else // BACK_TO_FRONT
        while(inside_volume){
            sampling_pos += ray_increment;
            inside_volume = inside_volume_bounds(sampling_pos);
        }
        
        inside_volume = true;

        while (inside_volume)
        {   
            s = get_sample_data(sampling_pos);
            vec4 color = texture(transfer_texture, vec2(s, s));

            float opacity = color.a; 
            if (ENABLE_OPACITY_CORRECTION == 1){
              opacity = (1-pow((1-opacity), (sampling_distance_ref/sampling_distance)));
            }
            inten = color.rgb*opacity + inten * (1-opacity); 

            vec4 light = vec4(0.0,0.0,0.0,1.0);

            /*
            if (ENABLE_LIGHTNING == 1){

                vec3 normal = normalize(get_gradient(sampling_pos)); 
                vec3 light_direction = normalize(light_position-sampling_pos);

                float cosTheta = clamp(dot(normal, light_direction), 0, 1); 

                vec3 eye = normalize(camera_location-sampling_pos);
                vec3 reflect = (-light_direction,normal); 

                float cosAlpha = clamp(dot(eye,reflect), 0, 1);

                float light_power = 7;
                float visibility = 1.0;

                light.rgb = light_ambient_color + 
                light_diffuse_color * light_power * cosTheta * visibility + 
                light_specular_color * light_power * pow(cosAlpha, light_ref_coef) * visibility;

                dst = vec4(inten.r, inten.g, inten.b, 1.0) * light;
              } */
              //else{
                dst = vec4(inten.r, inten.g, inten.b, 1.0);
              //}

            sampling_pos -= ray_increment; 
            inside_volume = inside_volume_bounds(sampling_pos);
            
        }
        // get sample
    #endif
#if ENABLE_OPACITY_CORRECTION == 1 // Opacity Correction
        
#else
        //float s = get_sample_data(sampling_pos);
#endif
        // dummy code
        
/*#if ENABLE_LIGHTNING == 1 // Add Shading
        IMPLEMENT;
#endif
*/        
    
#endif 

    // return the calculated color value
    FragColor = dst;
}