package net.ddsmedia.baceh.asistencia.qr.api;

import net.ddsmedia.baceh.asistencia.qr.entidad.Actualizar;
import net.ddsmedia.baceh.asistencia.qr.entidad.Asistenciados;
import net.ddsmedia.baceh.asistencia.qr.entidad.Beneficiarios;
import net.ddsmedia.baceh.asistencia.qr.entidad.Faltas;
import net.ddsmedia.baceh.asistencia.qr.entidad.FechaUltPase;
import net.ddsmedia.baceh.asistencia.qr.entidad.Listas;
import net.ddsmedia.baceh.asistencia.qr.entidad.LoginResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApi {

    //Pruebas server
    @GET("app/getListas/")
    Call<List<Listas>> listas();

    @GET("app/getIntegrantes/{lista}/{pagina}")
    Call<List<Beneficiarios>> listaIntegrantes(@Path("lista") String lista,
                                               @Path("pagina") int pagina);

    @GET("app/getUltimoPaseLista/{lista}")
    Call<List<FechaUltPase>> ultimaFechaPase(@Path("lista") int lista);

    @FormUrlEncoded
    @POST("app/postAccessUser/")
    Call<LoginResult> listaAccessUser(@Field("username") String username,
                                      @Field("password") String password);

    // sincronizarAsistencia (2)
    @FormUrlEncoded
    @POST("app/postAsistencia/{fk_titular}/{fecha}/{usuario}/{fechaDescarga}")
    Call<Asistenciados> listaAsistencia(@Field("fk_titular") String fk_titular,
                                        @Field("fecha") String fecha,
                                        @Field("usuario") String usuario,
                                        @Field("fechaDescarga") String fechaDescarga);

    // sincronizar (3)
    @FormUrlEncoded
    @POST("app/actualizar/{id_titular}/{ultima_visita}/{observaciones_asist}")
    Call<Actualizar>actualizarBeneficiario(
            @Field("id_titular") String id_titular,
            @Field("ultima_visita") String ultima_visita,
            @Field("observaciones_asist") String observaciones_asist);

    // sincronizarFaltas (1)
    @FormUrlEncoded
    @POST("app/actualizarFaltas/{id_titular}/{faltas}/{fechaDescarga}")
    Call<Faltas>actualizarFaltas(
            @Field("id_titular") String id_titular,
            @Field("faltas") String faltas,
            @Field("fechaDescarga") String fechaDescarga);



    /*

    //Pruebas local
    @GET("public/app/getListas/")
    Call<List<Listas>> listas();

    @GET("public/app/getIntegrantes/{lista}/{pagina}")
    Call<List<Beneficiarios>> listaIntegrantes(@Path("lista") String lista,
                                               @Path("pagina") int pagina);

    @GET("public/app/getUltimoPaseLista/{lista}")
    Call<List<FechaUltPase>> ultimaFechaPase(@Path("lista") int lista);

    @FormUrlEncoded
    @POST("public/app/postAccessUser/")
    Call<LoginResult> listaAccessUser(@Field("username") String username,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("public/app/postAsistencia/{fk_titular}/{fecha}/{usuario}")
    Call<Asistenciados> listaAsistencia(@Field("fk_titular") String fk_titular,
                                        @Field("fecha") String fecha,
                                        @Field("usuario") String usuario);

    @FormUrlEncoded
    @POST("public/app/actualizar/{id_titular}/{ultima_visita}/{observaciones_asist}")
    Call<Actualizar>actualizarBeneficiario(
            @Field("id_titular") String id_titular,
            @Field("ultima_visita") String ultima_visita,
            @Field("observaciones_asist") String observaciones_asist);
    */






    /*

    //Old
    @GET("getListas.php")
    Call<List<Listas>> listas();

    @GET("getIntegrantes.php")
    Call<List<Beneficiarios>> listaIntegrantes(@Query("lista") String lista,
                                               @Query("pagina") int pagina);

    @GET("getUltimoPaseLista.php")
    Call<List<FechaUltPase>> ultimaFechaPase(@Query("lista") int lista);

    @FormUrlEncoded
    @POST("postAccessUser.php")
    Call<LoginResult> listaAccessUser(@Field("username") String username,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("postAsistencia.php")
    Call<Asistenciados> listaAsistencia(@Field("fk_titular") String fk_titular,
                                        @Field("fecha") String fecha,
                                        @Field("usuario") String usuario);

    @FormUrlEncoded
    @POST("actualizar.php")
    Call<Actualizar>actualizarBeneficiario(
            @Field("id_titular") String id_titular,
            @Field("ultima_visita") String ultima_visita,
            @Field("observaciones_asist") String observaciones_asist
    );

     */
}
