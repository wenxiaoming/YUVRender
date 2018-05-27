precision mediump float;
varying   mediump vec2 vtexcoord;
attribute mediump vec4 position;
attribute mediump vec2 texcoord;
void main()
{
	gl_Position  = position;
	vtexcoord = texcoord;
}