package edu.pmdm.parcelableexample;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Asignatura implements Parcelable {
public String nombre;
public double nota;

    protected Asignatura(Parcel in) {
        nombre=in.readString();
        nota=in.readDouble();
    }

    public Asignatura(String nombre, double nota) {
        this.nombre = nombre;
        this.nota = nota;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeDouble(nota);
    }

    public String getNombre() {
        return nombre;
    }

    public double getNota() {
        return nota;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Asignatura> CREATOR = new Creator<Asignatura>() {
        @Override
        public Asignatura createFromParcel(Parcel in) {
            return new Asignatura(in);
        }

        @Override
        public Asignatura[] newArray(int size) {
            return new Asignatura[size];
        }



    };
    @Override
    public String toString() {
        return "{nombre= '" + nombre + '\''+", nota= '" + nota + '}';
    }
}
